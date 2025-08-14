import NavButton from '@/components/ui/button/NavButton/NavButton'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Button from '@/components/ui/button/Button'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import './LaasModal.less'
import React from 'react'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { TestComponentSelectors } from '#/mocks/Selectors'

type LaasButtonProps = {
	action: Function
	loading: boolean
	gruppeId: string | number
	children: any
}

export const LaasButton = ({ action, gruppeId, loading, children }: LaasButtonProps) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const matchMutate = useMatchMutate()

	if (loading) {
		return <Loading label="låser..." />
	}

	return (
		<React.Fragment>
			<Button
				data-testid={TestComponentSelectors.BUTTON_LAAS_GRUPPE}
				onClick={openModal}
				kind={'lock'}
			>
				LÅS
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="laasModal">
					<div className="laasModal laasModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Lås gruppen</h1>
						<h4>{children}</h4>
					</div>
					<div className="laasModal-actions">
						<NavButton onClick={closeModal} variant={'secondary'}>
							Nei
						</NavButton>
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_BEKREFT_LAAS}
							onClick={() => {
								closeModal()
								action(gruppeId)
								setTimeout(() => {
									matchMutate(REGEX_BACKEND_GRUPPER)
									matchMutate(REGEX_BACKEND_BESTILLINGER)
								}, 500)
							}}
							variant={'primary'}
						>
							Ja, jeg er sikker
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
