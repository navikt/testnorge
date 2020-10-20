import DollyModal from '~/components/ui/modal/DollyModal'
import { EndreMalnavn } from '~/pages/minSide/maler/EndreMalnavn'
import React from 'react'

export const MalModal = ({ bestilling, closeModal }) => (
	<DollyModal isOpen closeModal={closeModal} width="60%">
		<div>
			<h2>Navn på ny Mal</h2>
			<EndreMalnavn
				malInfo={{ id: bestilling.id, malNavn: bestilling.malNavn ? bestilling.malNavn : '' }}
				avbrytRedigering={closeModal}
			/>
		</div>
	</DollyModal>
)
