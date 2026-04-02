@import ru.mentee.power.crm.domain.Lead
@param Lead lead

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6 max-w-md mx-auto">
        <h2 class="text-2xl font-bold mb-6">Конвертация лида в сделку</h2>

        <div class="mb-4 p-3 bg-gray-50 rounded">
            <p><strong>Email:</strong> ${lead.getEmail()}</p>
            <p><strong>Компания:</strong> ${lead.getCompany()}</p>
            <p><strong>Статус:</strong> ${lead.getStatus()}</p>
        </div>

        <form method="post" action="/deals/convert" class="space-y-4">
            <input type="hidden" name="leadId" value="${lead.getId().toString()}">

            <div>
                <label for="amount" class="block text-sm font-medium text-gray-700 mb-1">
                    Сумма сделки (₽) <span class="text-red-500">*</span>
                </label>
                <input type="number"
                       step="0.01"
                       id="amount"
                       name="amount"
                       required
                       class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                       placeholder="10000.00">
            </div>

            <div class="flex gap-2 justify-end">
                <a href="/leads" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300">Отмена</a>
                <button type="submit" class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700">
                    Конвертировать
                </button>
            </div>
        </form>
    </div>
`)