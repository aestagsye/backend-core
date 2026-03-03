@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus
@param Lead lead

<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Редактирование лида</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="container mx-auto p-6">
    <div class="max-w-md mx-auto">
        <h1 class="text-2xl font-bold mb-6 text-gray-800">Редактирование лида</h1>

        <form method="post" action="/leads/${lead.id().toString()}" class="bg-white p-6 rounded-lg shadow-md">
            <!-- Скрытое поле для ID -->
            <input type="hidden" name="id" value="${lead.id().toString()}">

            <!-- Поле Email -->
            <div class="mb-4">
                <label for="email" class="block text-gray-700 text-sm font-semibold mb-2">Email:</label>
                <input type="email"
                       id="email"
                       name="email"
                       value="${lead.email()}"
                       class="shadow-sm border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-transparent"
                       required>
            </div>

            <!-- Поле Company -->
            <div class="mb-4">
                <label for="company" class="block text-gray-700 text-sm font-semibold mb-2">Компания:</label>
                <input type="text"
                       id="company"
                       name="company"
                       value="${lead.company()}"
                       class="shadow-sm border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-transparent"
                       required>
            </div>

            <!-- Поле Status (select) -->
            <div class="mb-6">
                <label for="status" class="block text-gray-700 text-sm font-semibold mb-2">Статус:</label>
                <select id="status"
                        name="status"
                        class="shadow-sm border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-transparent">
                    @if(lead.status() == LeadStatus.NEW)
                        <option value="NEW" selected>Новый</option>
                    @else
                        <option value="NEW">Новый</option>
                    @endif

                    @if(lead.status() == LeadStatus.CONTACTED)
                        <option value="CONTACTED" selected>Связывались</option>
                    @else
                        <option value="CONTACTED">Связывались</option>
                    @endif

                    @if(lead.status() == LeadStatus.QUALIFIED)
                        <option value="QUALIFIED" selected>Квалифицирован</option>
                    @else
                        <option value="QUALIFIED">Квалифицирован</option>
                    @endif
                </select>
            </div>

            <!-- Кнопки действий -->
            <div class="flex items-center justify-between">
                <button type="submit"
                        class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-offset-2 transition-colors">
                    Сохранить
                </button>
                <a href="/leads"
                   class="bg-gray-500 hover:bg-gray-600 text-white font-semibold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-offset-2 transition-colors">
                    Отмена
                </a>
            </div>
        </form>
    </div>
</div>
</body>
</html>