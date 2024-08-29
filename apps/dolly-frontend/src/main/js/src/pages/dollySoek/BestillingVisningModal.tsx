import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Bestillingskriterier from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import { Button } from '@navikt/ds-react'

export const BestillingVisningModal = ({ bestilling }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	if (!bestilling) {
		return null
	}

	return (
		<>
			<Button
				size="xsmall"
				variant="secondary"
				onClick={openModal}
				style={{ margin: '10px 10px 5px 0' }}
			>
				{bestilling.id}
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<h1>Bestilling #{bestilling.id}</h1>
				<Bestillingskriterier
					bestilling={bestilling}
					bestillingsinformasjon={null}
					header="Bestillingskriterier"
				/>
			</DollyModal>
		</>
	)
}

export const BestillingVisningListe = ({ bestillinger }) => {
	return (
		<div className="flexbox--flex-wrap">
			{bestillinger?.map((bestilling) => (
				<BestillingVisningModal key={bestilling.id} bestilling={bestilling} />
			))}
		</div>
	)
}
