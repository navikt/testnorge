import { Button } from '@navikt/ds-react'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { TrashIcon } from '@navikt/aksel-icons'

export const DeleteOrganisasjon = ({ orgNr, mutate }) => {
	const slettOrg = () => {
		OrganisasjonTilgangService.deleteOrganisasjoner(orgNr).then((response) => {
			mutate()
		})
	}

	return (
		<>
			<Button onClick={slettOrg} variant={'tertiary'} icon={<TrashIcon />} size={'small'} />
		</>
	)
}
