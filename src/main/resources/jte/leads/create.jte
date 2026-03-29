@import ru.mentee.power.crm.domain.Lead
@import ru.mentee.power.crm.domain.LeadStatus

@param Lead lead = null

@template.layout.main(
content = @`
    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Добавить нового лида</h1>

        <form action="/leads" method="post" class="space-y-4">
            <!-- Email -->
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    Email <span class="text-red-500">*</span>
                </label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        required
                        value="${lead != null ? lead.getEmail() : ""}"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="example@company.com"
                />
            </div>

            <!-- Company -->
            <div>
                <label for="company" class="block text-sm font-medium text-gray-700 mb-1">
                    Company <span class="text-red-500">*</span>
                </label>
                <input
                        type="text"
                        id="company"
                        name="company"
                        required
                        value="${lead != null ? lead.getCompany() : ""}"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="Company name"
                />
            </div>

            <!-- Status -->
            <div>
                <label for="status" class="block text-sm font-medium text-gray-700 mb-1">
                    Статус <span class="text-red-500">*</span>
                </label>
                <select
                        id="status"
                        name="status"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                >
                    @if(lead != null && lead.getStatus() == LeadStatus.NEW)
                        <option value="NEW" selected>NEW</option>
                    @else
                        <option value="NEW">NEW</option>
                    @endif

                    @if(lead != null && lead.getStatus() == LeadStatus.CONTACTED)
                        <option value="CONTACTED" selected>CONTACTED</option>
                    @else
                        <option value="CONTACTED">CONTACTED</option>
                    @endif

                    @if(lead != null && lead.getStatus() == LeadStatus.QUALIFIED)
                        <option value="QUALIFIED" selected>QUALIFIED</option>
                    @else
                        <option value="QUALIFIED">QUALIFIED</option>
                    @endif
                </select>
            </div>

            <!-- Submit Button -->
            <button
                    type="submit"
                    class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 transition-colors"
            >
                Создать лида
            </button>

            <!-- Cancel Link -->
            <a
                    href="/leads"
                    class="block text-center text-sm text-gray-600 hover:text-gray-900"
            >
                Отмена
            </a>
        </form>
    </div>
`
)