package org.caselli.comparativecognitiveworkflow.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AI4NeService {

    private final ChatClient chatClient;

    private final ToolService toolService;

    private final Logger  logger = Logger.getLogger(AI4NeService.class.getName());

    // Inject ChatClient through the constructor
    public AI4NeService(ChatClient.Builder chatClientBuilder, ToolService toolService) {

        this.chatClient = chatClientBuilder.build();
        this.toolService = toolService;
    }

    public Object performRoutingWithSimpleLLM(String inputRequest) {
        logger.info("Fetching devices data from external service to enrich the context for routing request: " + inputRequest);

        // Get the list of devices in advance to enrich the context
        String devices = toolService.fetchDevices();
        logger.info("Fetched devices data: " + devices);

        // Get the topology of the network to enrich the context
        String networkTopology = toolService.fetchNetworkTopology();
        logger.info("Fetched network topology: " + networkTopology);







        logger.info("Performing routing for request: " + inputRequest);

        String systemPromptTemplateString = """
        <AgentProfile>
            You are a highly specialized **AI for Network Engineering (AI4NE) agent**. Your core responsibility is to act as a **smart router**, dynamically finding the absolute best network path and resources for any user request. You achieve this by intelligently matching user needs with the capabilities of available network devices.
        </AgentProfile>

        <TaskOverview>
            When a user submits a request with specific needs, your job is to:
            * **Understand the Goal:** Decode what the user wants to accomplish and their exact technical specifications.
            * **Inventory Devices:** Review all available network devices, assessing their:
                * **Performance:** (e.g., processing power, bandwidth)
                * **Green Footprint:** (e.g., energy efficiency)
                * **Speed:** (e.g., latency, data path efficiency)
                * **Quality:** (e.g., model compatibility, output fidelity)
                * **Compliance:** (e.g., security patches, licenses, data sovereignty)
            * **Filter & Select:** Discard any devices that *cannot* meet the core requirements (e.g., too slow, non-compliant, incompatible). From the remaining, pick the ones that offer the most optimal balance of performance, efficiency, and precise fit.
            * **Report Back:** Provide only the identifiers of the chosen devices.
        </TaskOverview>

        <ExecutionSteps>
            1.  **Parse User Request:** Extract the user's intent and all associated service requirements.
            2.  **Evaluate Devices:** For each device in <AvailableDevices>{devices}</AvailableDevices>, analyze its hardware, software capabilities, compliance status, and network metrics.
            3.  **Apply Hard Constraints:** Immediately filter out any devices that violate critical constraints like compliance (e.g., unpatched software, licensing issues, data residency conflicts), unsupported models, or excessive latency.
            4.  **Prioritize and Select:** From the filtered pool, choose the devices that best satisfy the user's requirements, prioritizing sustainability, performance, and overall best fit.
            5.  **Output Device IDs:** Generate a list of the selected device IDs.
        </ExecutionSteps>

        <OutputGuidelines>
            Your response must be a **comma-separated list of device IDs**. Absolutely do not invent or include any IDs that were not present in the initial <AvailableDevices> input.
            Example: device_A, device_B, device_C
            Do not respond in natual language or provide any additional commentary. The output must be strictly formatted as specified.
        </OutputGuidelines>

        <GuidingPrinciples>
            * **Non-Negotiable Constraints:** Be extremely strict with all hard constraints, especially security and compliance.
            * **Optimal Resource Allocation:** When choices exist, always aim for the most efficient and effective device selection.
        </GuidingPrinciples>
        """;


        SystemPromptTemplate systemPrompt = new SystemPromptTemplate(systemPromptTemplateString);

        Message systemMessage = systemPrompt.createMessage(Map.of("devices", devices));

        Message userMessage = new UserMessage(inputRequest);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        System.out.println("Prompt created: " + prompt.toString());

        // Use the ChatClient to send a prompt to the LLM
        Object route = chatClient.prompt(prompt)
                .call()
                .content();

        System.out.println("LLM response: " + route);

        return route;
    }


    public Object performRoutingWithFunctionCallingLLM(String inputRequest) {


        String systemPromptTemplateString = """
        <AgentProfile>
            You are a highly specialized **AI for Network Engineering (AI4NE) agent**. Your core responsibility is to act as a **smart router**, dynamically finding the absolute best network path and resources for any user request. You achieve this by intelligently matching user needs with the capabilities of available network devices.
        </AgentProfile>

        <TaskOverview>
            When a user submits a request with specific needs, your job is to:
            - 1 **Understand the Goal:** Decode what the user wants to accomplish and their exact technical specifications.
            - 2 **Inventory Devices:** Fetch and review all available network devices, assessing their:
                * Performance: (e.g., processing power, bandwidth)
                * Green Footprint: (e.g., energy efficiency)
                * Speed: (e.g., latency, data path efficiency)
                * Quality: (e.g., model compatibility, output fidelity)
                * Compliance: (e.g., security patches, licenses, data sovereignty)
            - 3 **Filter & Select:** Discard any devices that *cannot* meet the core requirements (e.g., too slow, non-compliant, incompatible). From the remaining, pick the ones that offer the most optimal balance of performance, efficiency, and precise fit.
            - 4 **Route the Request:** Use the selected devices as constraints to find the best network path that meets the user's needs using the `route` tool function.
            - 5 **Finalize routing:** By analyzing the network topology and the paths found in *step 4*, determine the most appropriate devices to route the request through considering the user's requirements.
            - 6 **Report Back:** Provide only the chosen path
        </TaskOverview>



        <OutputGuidelines>
            Your response must be a path in the network topology. 
            The path must be a list of ids where the ids are the identifiers of the nodes in the network topology (not the device ids).
          
            
        </OutputGuidelines>

        <GuidingPrinciples>
            * **Non-Negotiable Constraints:** Be extremely strict with all hard constraints, especially security and compliance.
            * **Optimal Resource Allocation:** When choices exist, always aim for the most efficient and effective device selection.
        </GuidingPrinciples>
        """;


        SystemPromptTemplate systemPrompt = new SystemPromptTemplate(systemPromptTemplateString);
        Message systemMessage = systemPrompt.createMessage();
        Message userMessage = new UserMessage(inputRequest);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        System.out.println("Prompt created: " + prompt.toString());

        // Use the ChatClient to send a prompt to the LLM
        List<?> path = chatClient.prompt(prompt)
                .tools(toolService)
                .call()
                .entity(List.class);


        System.out.println("LLM response: " + path);

        return path;

    }


}