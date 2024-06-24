import { Button } from '@navikt/ds-react'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { TrashIcon } from '@navikt/aksel-icons'

export const DeleteOrganisasjon = ({ orgNr }) => {
	const slettOrg = () => {
		OrganisasjonTilgangService.deleteOrganisasjoner(orgNr).then((response) => {
			console.log('response: ', response) //TODO - SLETT MEG
		})
	}

	return (
		<>
			<Button onClick={slettOrg} variant={'tertiary'} icon={<TrashIcon />} size={'small'} />
		</>
	)
}
