import DollyModal from '~/components/ui/modal/DollyModal'
import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'

export const KommentarModal = ({ ident, closeModal, iLaastGruppe }) => {
	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="50%" overflow="auto">
				<div className="modal">
					<h1>Endre kommentar</h1>
					<br />
					<BeskrivelseConnector ident={ident} iLaastGruppe={iLaastGruppe} closeModal={closeModal} />
				</div>
			</DollyModal>
		</ErrorBoundary>
	)
}
