import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { ImportTilDollyModal } from './ImportTilDollyModal'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface ImportTilDolly {
	valgtePersoner: Array<string>
}

export const ImportTilDolly = ({ valgtePersoner }: ImportTilDolly) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	const personerValgt = valgtePersoner.length > 0
	return (
		<ErrorBoundary>
			<div className="flexbox--align-center--justify-end">
				<NavButton
					type="hoved"
					onClick={openModal}
					disabled={!personerValgt}
					title={!personerValgt ? 'Velg personer' : null}
				>
					Importer person(er) til gruppe
				</NavButton>
				<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
					<ImportTilDollyModal onAvbryt={closeModal} valgtePersoner={valgtePersoner} />
				</DollyModal>
			</div>
		</ErrorBoundary>
	)
}
