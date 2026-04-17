import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { OrgOversikt } from '@/pages/adminPages/Orgtilgang/OrgOversikt'
import { OrgtilgangForm } from '@/pages/adminPages/Orgtilgang/OrgtilgangForm'
import { useOrganisasjonTilgang } from '@/utils/hooks/useOrganisasjonTilgang'
import { useErDollyAdmin } from '@/utils/DollyAdmin'

export default () => {
	const isAdmin = useErDollyAdmin()
	const { organisasjonTilgang, loading, error, mutate } = useOrganisasjonTilgang()

	if (!isAdmin) {
		return <AdminAccessDenied />
	}

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
