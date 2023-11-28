import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import DollyModal from '@/components/ui/modal/DollyModal'
import Bestillingskriterier from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'

export const BestillingVisningModal = ({ bestilling }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	if (!bestilling) {
		return null
	}

	return (
		<div className="flexbox--align-center--justify-end">
			<Button onClick={openModal}>{bestilling.id}</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<h1>Bestilling #{bestilling.id}</h1>
				<Bestillingskriterier
					bestilling={bestilling}
					bestillingsinformasjon={null}
					header="Bestillingskriterier"
				/>
			</DollyModal>
		</div>
	)
}

export const BestillingVisningListe = ({ bestillinger }) => {
	return (
		<div className="flexbox--flex-wrap">
			{bestillinger.map((bestilling) => (
				<BestillingVisningModal key={bestilling.id} bestilling={bestilling} />
			))}
		</div>
	)
}
