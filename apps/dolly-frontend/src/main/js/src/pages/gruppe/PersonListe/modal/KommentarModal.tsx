import { DollyModal } from '@/components/ui/modal/DollyModal'

import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import BeskrivelseConnector from '@/components/beskrivelse/BeskrivelseConnector'

export const KommentarModal = ({ ident, closeModal }) => {
	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="50%" overflow="auto">
				<h1>Endre kommentar</h1>
				<br />
				<BeskrivelseConnector ident={ident} closeModal={closeModal} />
			</DollyModal>
		</ErrorBoundary>
	)
}
