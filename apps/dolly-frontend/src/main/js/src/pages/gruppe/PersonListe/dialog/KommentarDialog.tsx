import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import BeskrivelseConnector from '@/components/beskrivelse/BeskrivelseConnector'
import { Dialog } from '@navikt/ds-react'

type Props = {
	ident: { beskrivelse: string; ident: number }
	closeDialog: () => void
}

export const KommentarDialog = ({ ident, closeDialog }: Props) => (
	<ErrorBoundary>
		<Dialog open onOpenChange={(open) => !open && closeDialog()}>
			<Dialog.Popup width="medium">
				<Dialog.Header>
					<Dialog.Title>Endre kommentar</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BeskrivelseConnector ident={ident} closeDialog={closeDialog} />
				</Dialog.Body>
			</Dialog.Popup>
		</Dialog>
	</ErrorBoundary>
)
