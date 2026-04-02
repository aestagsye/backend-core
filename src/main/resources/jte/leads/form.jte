@import ru.mentee.power.crm.domain.Lead
@import ru.mentee.power.crm.domain.LeadStatus
@param Lead lead
@param org.springframework.validation.BindingResult errors = null
@param String formAction
@param String submitButtonText

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6 max-w-2xl mx-auto">
        <h2 class="text-2xl font-bold mb-6">${submitButtonText} лида</h2>

        @if(errors != null && errors.hasGlobalErrors())
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                <ul class="list-disc list-inside">
                    @for(var error : errors.getGlobalErrors())
                        <li>${error.getDefaultMessage()}</li>
                    @endfor
                </ul>
            </div>
        @endif

        <form method="post" action="${formAction}" class="space-y-4">
            <!-- Email -->
            <div class="mb-4">
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input type="email"
                       id="email"
                       name="email"
                       value="${lead.getEmail() != null ? lead.getEmail() : ""}"
                       class="w-full px-3 py-2 border ${errors != null && errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"} rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500">
                @if(errors != null && errors.hasFieldErrors("email"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("email").getDefaultMessage()}</p>
                @endif
            </div>

            <!-- Company -->
            <div class="mb-4">
                <label for="company" class="block text-sm font-medium text-gray-700 mb-1">Company</label>
                <input type="text"
                       id="company"
                       name="company"
                       value="${lead.getCompany() != null ? lead.getCompany() : ""}"
                       class="w-full px-3 py-2 border ${errors != null && errors.hasFieldErrors("company") ? "border-red-500" : "border-gray-300"} rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500">
                @if(errors != null && errors.hasFieldErrors("company"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("company").getDefaultMessage()}</p>
                @endif
            </div>

            <!-- Status -->
            <div class="mb-4">
                <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Status</label>
                <select id="status"
                        name="status"
                        class="w-full px-3 py-2 border ${errors != null && errors.hasFieldErrors("status") ? "border-red-500" : "border-gray-300"} rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500">
                    <option value="NEW" selected="${lead.getStatus() == LeadStatus.NEW}">NEW</option>
                    <option value="CONTACTED" selected="${lead.getStatus() == LeadStatus.CONTACTED}">CONTACTED</option>
                    <option value="QUALIFIED" selected="${lead.getStatus() == LeadStatus.QUALIFIED}">QUALIFIED</option>
                    <option value="CONVERTED" selected="${lead.getStatus() == LeadStatus.CONVERTED}">CONVERTED</option>
                </select>
                @if(errors != null && errors.hasFieldErrors("status"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("status").getDefaultMessage()}</p>
                @endif
            </div>

            <!-- Кнопки -->
            <div class="flex gap-2 justify-end">
                <a href="/leads" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300">Отмена</a>
                <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">${submitButtonText}</button>
            </div>
        </form>
    </div>
`)