import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { OrgOversikt } from '@/pages/adminPages/Orgtilgang/OrgOversikt'

export default () => {
	const erAdmin = true //TODO: Implementer sjekk for admin
	// const erAdmin = false //TODO: Implementer sjekk for admin

	if (!erAdmin) {
		return <AdminAccessDenied />
	}

	return (
		<>
			<h1>Organisasjon-tilgang</h1>
			<OrgOversikt />
		</>
	)
}
