import React, { useState } from 'react'
import { Alert, BodyShort, Button, Tag } from '@navikt/ds-react'
import { InfoStripeType, useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { EditInfostripeForm } from './EditInfostripeForm'
import { TestComponentSelectors } from '#/mocks/Selectors'

interface Props {
	stripe: InfoStripeType
}
export const InfostripeListItem: React.FC<Props> = ({ stripe }) => {
	const { deleteInfostripe } = useDollyInfostriper()
	const [editing, setEditing] = useState(false)
	const [confirm, setConfirm] = useState(false)
	const [deleting, setDeleting] = useState(false)

	const handleDelete = async () => {
		setDeleting(true)
		try {
			await deleteInfostripe(stripe.id)
		} finally {
			setDeleting(false)
		}
	}

	return (
		<li style={{ marginBottom: '1rem' }} data-testid={TestComponentSelectors.INFOSTRIPE_ITEM}>
			<Alert
				variant={stripe.type?.toLowerCase() as 'info' | 'warning' | 'error' | 'success'}
				style={{ position: 'relative' }}
			>
				<div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', flexWrap: 'wrap' }}>
					<Tag data-color="neutral" size="small" variant="strong">
						{stripe.id}
					</Tag>
					<Tag data-color="neutral" size="small" variant="outline">
						{stripe.type.toUpperCase()}
					</Tag>
					<BodyShort size="small">
						{new Date(stripe.start).toLocaleString()} →{' '}
						{stripe.expires ? new Date(stripe.expires).toLocaleString() : 'Ingen sluttdato'}
					</BodyShort>
				</div>
				<div style={{ marginTop: '.5rem', whiteSpace: 'pre-wrap' }}>{stripe.message}</div>
				<div style={{ display: 'flex', gap: '.5rem', marginTop: '.75rem' }}>
					{!editing && (
						<Button
							size="xsmall"
							variant="secondary"
							onClick={() => setEditing(true)}
							data-testid={TestComponentSelectors.BUTTON_EDIT_INFOSTRIPE}
						>
							Endre
						</Button>
					)}
					{!editing && !confirm && (
						<Button
							data-color="danger"
							size="xsmall"
							variant="primary"
							onClick={() => setConfirm(true)}
							data-testid={TestComponentSelectors.BUTTON_DELETE_INFOSTRIPE}
						>
							Slett
						</Button>
					)}
					{confirm && !editing && (
						<>
							<Button
								data-color="danger"
								size="xsmall"
								variant="primary"
								loading={deleting}
								onClick={handleDelete}
							>
								Bekreft slett
							</Button>
							<Button size="xsmall" variant="secondary" onClick={() => setConfirm(false)}>
								Avbryt
							</Button>
						</>
					)}
				</div>
				{editing && (
					<div style={{ marginTop: '1rem' }}>
						<EditInfostripeForm stripe={stripe} onDone={() => setEditing(false)} />
					</div>
				)}
			</Alert>
		</li>
	)
}
