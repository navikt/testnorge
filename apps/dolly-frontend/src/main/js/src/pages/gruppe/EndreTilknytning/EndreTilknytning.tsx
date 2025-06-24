import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import React from 'react'
import { EndreTilknytningModal } from '@/pages/gruppe/EndreTilknytning/EndreTilknytningModal'

export const EndreTilknytning = ({ gruppe }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<>
			<Button onClick={openModal} kind="rediger-person">
				BYTT EIER
			</Button>
			{modalIsOpen && (
				<EndreTilknytningModal gruppe={gruppe} modalIsOpen={modalIsOpen} closeModal={closeModal} />
			)}
		</>
	)
}
