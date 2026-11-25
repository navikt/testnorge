import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import BeskrivelseConnector from '@/components/beskrivelse/BeskrivelseConnector'
import { Dialog } from '@navikt/ds-react'

type Props = {
	ident: { beskrivelse: string; ident: number }
	closeModal: () => void
}

export const KommentarModal = ({ ident, closeModal }: Props) => (
	<ErrorBoundary>
		<Dialog open onOpenChange={(open) => !open && closeModal()}>
			<Dialog.Popup width="medium">
				<Dialog.Header>
					<Dialog.Title>Endre kommentar</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BeskrivelseConnector ident={ident} closeModal={closeModal} />
				</Dialog.Body>
			</Dialog.Popup>
		</Dialog>
	</ErrorBoundary>
)
