import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { Alert } from '@navikt/ds-react'
import { AppstyringTable } from '@/pages/adminPages/Appstyring/AppstyringTable'
import { erDollyAdmin } from '@/utils/DollyAdmin'

export default () => {
	if (!erDollyAdmin()) {
		return <AdminAccessDenied />
	}

	//TODO: Implementer henting av data fra backend
	const dataMock = [
		{ parameter: 'parameter1', verdi: 'verdi1' },
		{ parameter: 'parameter2', verdi: 'verdi2' },
		{ parameter: 'parameter3', verdi: 'verdi3' },
		{ parameter: 'parameter4', verdi: 'verdi4' },
		{ parameter: 'parameter5', verdi: 'verdi5' },
		{ parameter: 'parameter6', verdi: 'verdi6' },
		{ parameter: 'parameter7', verdi: 'verdi7' },
		{ parameter: 'parameter8', verdi: 'verdi8' },
		{ parameter: 'parameter9', verdi: 'verdi9' },
		{ parameter: 'parameter10', verdi: 'verdi10' },
	]

	return (
		<>
			<h1>App-styring</h1>
			<Alert variant={'info'} style={{ marginBottom: '15px' }}>
				Denne siden er under utvikling.
			</Alert>
			<AppstyringTable data={dataMock} />
		</>
	)
}
