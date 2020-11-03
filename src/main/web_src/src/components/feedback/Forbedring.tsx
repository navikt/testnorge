import * as React from 'react'
// @ts-ignore
import './Forbedring.less'
import { ForbedringModal } from '~/components/feedback/ForbedringModal'
import useBoolean from '~/utils/hooks/useBoolean'
import Icon from '~/components/ui/icon/Icon'

export const Forbedring = () => {
	const [isForbedringModalOpen, openForbedringModal, closeForbedringModal] = useBoolean(false)

	return (
		<div>
			<button className="btn-modal" onClick={openForbedringModal}>
				<Icon kind="krr" size={28} />
			</button>
			{isForbedringModalOpen && <ForbedringModal closeModal={closeForbedringModal} />}
		</div>
	)
}
