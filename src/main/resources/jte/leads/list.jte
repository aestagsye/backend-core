@import ru.mentee.power.crm.model.LeadStatus
@param java.util.List<ru.mentee.power.crm.model.Lead> leads
@param LeadStatus currentFilter

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Lead List</h2>

        <!-- Блок фильтров по статусам -->
        <div class="mb-4 flex gap-2">
            <a href="/leads"
               class="px-4 py-2 rounded ${currentFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}">
                Все
            </a>
            <a href="/leads?status=NEW"
               class="px-4 py-2 rounded ${currentFilter == LeadStatus.NEW ? "bg-blue-500 text-white" : "bg-gray-200"}">
                NEW
            </a>
            <a href="/leads?status=CONTACTED"
               class="px-4 py-2 rounded ${currentFilter == LeadStatus.CONTACTED ? "bg-blue-500 text-white" : "bg-gray-200"}">
                CONTACTED
            </a>
            <a href="/leads?status=QUALIFIED"
               class="px-4 py-2 rounded ${currentFilter == LeadStatus.QUALIFIED ? "bg-blue-500 text-white" : "bg-gray-200"}">
                QUALIFIED
            </a>
        </div>

        <div class="mb-4 flex justify-between items-center">
            <a href="/leads/new"
               class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
                + Добавить лида
            </a>
        </div>

        @if(currentFilter != null)
            <p class="text-sm text-gray-600 mb-2">Показаны лиды со статусом: ${currentFilter}</p>
        @endif

        <table class="min-w-full bg-white border border-gray-200">
            <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Email</th>
                <th class="px-4 py-2 text-left">Company</th>
                <th class="px-4 py-2 text-left">Status</th>
                <th class="px-4 py-2 text-left">Действия</th>
            </tr>
            </thead>
            <tbody>
            @if(leads != null && !leads.isEmpty())
                @for(var lead : leads)
                    <tr class="border-t hover:bg-gray-50">
                        <td class="px-4 py-2">${lead.email()}</td>
                        <td class="px-4 py-2">${lead.company()}</td>
                        <td class="px-4 py-2">
                            @if(lead.status() == LeadStatus.NEW)
                                <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">${lead.status()}</span>
                            @elseif(lead.status() == LeadStatus.CONTACTED)
                                <span class="px-2 py-1 rounded text-sm bg-yellow-100 text-yellow-800">${lead.status()}</span>
                            @else
                                <span class="px-2 py-1 rounded text-sm bg-purple-100 text-purple-800">${lead.status()}</span>
                            @endif
                        </td>
                        <td class="px-4 py-2">
                            <a href="/leads/${lead.id().toString()}/edit"
                               class="text-blue-600 hover:text-blue-800 hover:underline font-medium">
                                Редактировать
                            </a>
                            <form method="post" action="/leads/${lead.id().toString()}/delete" class="inline">
                                <button type="submit"
                                        class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
                                        onclick="return confirm('Удалить лида с почтой ${lead.email()}?')">
                                    Удалить
                                </button>
                            </form>
                        </td>
                    </tr>
                @endfor
            @else
                <tr>
                    <td colspan="4" class="px-4 py-2 text-center text-gray-500">
                        Нет лидов для отображения
                    </td>
                </tr>
            @endif
            </tbody>
        </table>
    </div>
`)