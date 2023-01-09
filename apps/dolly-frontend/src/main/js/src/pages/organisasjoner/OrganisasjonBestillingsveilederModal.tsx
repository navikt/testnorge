import DollyModal from '@/components/ui/modal/DollyModal'

import { NyBestillingProps } from '@/components/bestillingsveileder/startModal/NyIdent/NyIdent'
import { NyOrganisasjon } from '@/pages/organisasjoner/NyOrganisasjon/NyOrganisasjon'

export const OrganisasjonBestillingsveilederModal = ({
	onAvbryt,
	onSubmit,
	brukernavn,
}: NyBestillingProps) => {
	return (
		<DollyModal isOpen closeModal={onAvbryt} width="60%" overflow="auto">
			<div className="start-bestilling-modal">
				<h1>Opprett organisasjon</h1>

				<NyOrganisasjon brukernavn={brukernavn} onSubmit={onSubmit} onAvbryt={onAvbryt} />
			</div>
		</DollyModal>
	)
}
