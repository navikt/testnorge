import * as React from 'react'
// @ts-ignore
import './Forbedring.less'
import { ForbedringModal } from '@/components/feedback/ForbedringModal'
import useBoolean from '@/utils/hooks/useBoolean'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { CypressSelector } from '../../../cypress/mocks/Selectors'

export const Forbedring = () => {
	const [isForbedringModalOpen, openForbedringModal, closeForbedringModal] = useBoolean(false)

	return (
		<ErrorBoundary>
			<button
				data-testid={CypressSelector.BUTTON_OPEN_FORBEDRING_MODAL}
				className="btn-modal"
				onClick={openForbedringModal}
			>
				<Icon kind="krr" fontSize={'2rem'} />
			</button>
			{isForbedringModalOpen && <ForbedringModal closeModal={closeForbedringModal} />}
		</ErrorBoundary>
	)
}
