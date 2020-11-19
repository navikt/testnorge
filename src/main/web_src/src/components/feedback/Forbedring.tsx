import * as React from 'react'
// @ts-ignore
import './Forbedring.less'
import { ForbedringModal } from '~/components/feedback/ForbedringModal'
import useBoolean from '~/utils/hooks/useBoolean'
import Icon from '~/components/ui/icon/Icon'

type Props = {
	brukerBilde: Response
}

export const Forbedring = ({ brukerBilde }: Props) => {
	const [isForbedringModalOpen, openForbedringModal, closeForbedringModal] = useBoolean(false)

	return (
		<div>
			<button className="btn-modal" onClick={openForbedringModal}>
				<Icon kind="krr" size={33} />
			</button>
			{isForbedringModalOpen && (
				<ForbedringModal closeModal={closeForbedringModal} brukerBilde={brukerBilde} />
			)}
		</div>
	)
}
