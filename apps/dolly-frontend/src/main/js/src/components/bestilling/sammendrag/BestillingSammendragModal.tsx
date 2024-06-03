import useBoolean from '@/utils/hooks/useBoolean'
import DollyModal from '@/components/ui/modal/DollyModal'
import Button from '@/components/ui/button/Button'
import BestillingSammendrag from '@/components/bestilling/sammendrag/BestillingSammendrag'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'

export const BestillingSammendragModal = ({ bestilling }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	if (!bestilling) {
		return null
	}

	return (
		<div className="flexbox--align-center--justify-end">
			<Button
				data-testid={CypressSelector.BUTTON_OPEN_BESTILLINGSDETALJER}
				onClick={openModal}
				kind="details"
			>
				BESTILLINGSDETALJER
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<h1>Bestilling #{bestilling.id}</h1>
				<BestillingSammendrag bestilling={bestilling} modal />
			</DollyModal>
		</div>
	)
}
