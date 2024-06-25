import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { OrgOversikt } from '@/pages/adminPages/Orgtilgang/OrgOversikt'
import { OrgtilgangForm } from '@/pages/adminPages/Orgtilgang/OrgtilgangForm'
import { useOrganisasjonTilgang } from '@/utils/hooks/useOrganisasjonTilgang'

export default () => {
	const erAdmin = true //TODO: Implementer sjekk for admin

	if (!erAdmin) {
		return <AdminAccessDenied />
	}

	const { organisasjonTilgang, loading, error, mutate } = useOrganisasjonTilgang()

	//TODO: Slett organisasjon-tilgang-frontend?

	return (
		<>
			<h1>Organisasjon-tilgang</h1>
			<OrgtilgangForm mutate={mutate} />
			<OrgOversikt
				organisasjonTilgang={organisasjonTilgang}
				loading={loading}
				error={error}
				mutate={mutate}
			/>
		</>
	)
}
