import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { Alert } from '@navikt/ds-react'

export default () => {
	const erAdmin = true //TODO: Implementer sjekk for admin

	if (!erAdmin) {
		return <AdminAccessDenied />
	}

	return (
		<>
			<h1>App-styring</h1>
			<Alert variant={'info'}>Denne siden er under utvikling.</Alert>
		</>
	)
}
