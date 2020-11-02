import * as React from 'react'
// @ts-ignore
import './Forbedring.less'
import { ForbedringModal } from '~/components/feedback/ForbedringModal'
import useBoolean from '~/utils/hooks/useBoolean'

export const Forbedring = () => {
	const [isForbedringModalOpen, openForbedringModal, closeForbedringModal] = useBoolean(false)

	return (
		<div className="modal-link">
			<button className="btn-modal" onClick={openForbedringModal}>
				Forbedring
			</button>
			{isForbedringModalOpen && <ForbedringModal closeModal={closeForbedringModal} />}
		</div>
	)
}
