@import ru.mentee.power.crm.domain.Lead
@import ru.mentee.power.crm.domain.LeadStatus
@param java.util.List<Lead> leads
@param LeadStatus currentFilter
@param String search = null

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Lead List</h2>
        <!-- Форма поиска -->
        <div class="bg-white shadow rounded-lg p-4 mb-6">
            <form method="get" action="/leads" class="flex gap-2 items-end">
                <div class="flex-1">
                    <label for="search" class="block text-sm font-medium text-gray-700 mb-1">Поиск по почте или компании</label>
                    <input type="text"
                           id="search"
                           name="search"
                           value="${search != null ? search : ""}"
                           placeholder="Введите текст..."
                           class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500">
                </div>
                @if(currentFilter != null)
                    <input type="hidden" name="status" value="${currentFilter}">
                @endif
                <button type="submit"
                        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
                    Найти
                </button>
                <a href="/leads"
                   class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 text-center">
                    Сбросить
                </a>
            </form>
        </div>
        <!-- Блок фильтров по статусам -->
        <div class="mb-4 flex gap-2">
            <a href="/leads${search != null ? "?search=" + search : ""}"
               class="px-4 py-2 rounded ${currentFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}">
                Все
            </a>
            <a href="/leads?status=NEW${search != null ? "&search=" + search : ""}"
               class="px-4 py-2 rounded ${currentFilter == LeadStatus.NEW ? "bg-blue-500 text-white" : "bg-gray-200"}">
                NEW
            </a>
            <a href="/leads?status=CONTACTED${search != null ? "&search=" + search : ""}"
               class="px-4 py-2 rounded ${currentFilter == LeadStatus.CONTACTED ? "bg-blue-500 text-white" : "bg-gray-200"}">
                CONTACTED
            </a>
            <a href="/leads?status=QUALIFIED${search != null ? "&search=" + search : ""}"
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
                        <td class="px-4 py-2">${lead.getEmail()}</td>
                        <td class="px-4 py-2">${lead.getCompany()}</td>
                        <td class="px-4 py-2">
                            @if(lead.getStatus() == LeadStatus.NEW)
                                <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">${lead.getStatus()}</span>
                            @elseif(lead.getStatus() == LeadStatus.CONTACTED)
                                <span class="px-2 py-1 rounded text-sm bg-yellow-100 text-yellow-800">${lead.getStatus()}</span>
                            @else
                                <span class="px-2 py-1 rounded text-sm bg-purple-100 text-purple-800">${lead.getStatus()}</span>
                            @endif
                        </td>
                        <td class="px-4 py-2">
                            <a href="/leads/${lead.getId().toString()}/edit"
                               class="text-blue-600 hover:text-blue-800 hover:underline font-medium">
                                Редактировать
                            </a>
                            <form method="post" action="/leads/${lead.getId().toString()}/delete" class="inline">
                                <button type="submit"
                                        class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
                                        onclick="return confirm('Удалить лида с почтой ${lead.getEmail()}?')">
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