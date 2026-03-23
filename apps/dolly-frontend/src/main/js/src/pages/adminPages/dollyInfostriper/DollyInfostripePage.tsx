import React from 'react'
import { useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { CreateInfostripeForm } from '@/pages/adminPages/dollyInfostriper/CreateInfostripeForm'
import { ActionMenu, Alert, Box } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import { InfostripeListItem } from '@/pages/adminPages/dollyInfostriper/InfostripeListItem'
import { TestComponentSelectors } from '#/mocks/Selectors'

export default function DollyInfostripePage() {
	const { infostriper, loading, error } = useDollyInfostriper(true)

	return (
		<>
			<h1>Infostripe-oversikt</h1>
			<p>Her finner du en oversikt over alle eksisterende infostriper som vises i Dolly.</p>
			<ActionMenu.Divider style={{ marginTop: '20px' }} />
			<CreateInfostripeForm />
			{loading && (
				<div data-testid={TestComponentSelectors.LOADING_INFOSTRIPER}>
					<Loading label="Laster infostriper ..." />
				</div>
			)}
			{error && <Alert variant="warning">{`Feil ved henting av data: ${error.message}`}</Alert>}
			{infostriper?.length > 0 && (
				<>
					<ActionMenu.Divider style={{ marginTop: '20px' }} />
					<h2>Aktive infostriper</h2>
					<Box background="default" padding="space-16" style={{ marginTop: '1.5rem' }}>
						<ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
							{infostriper
								?.sort((a, b) => new Date(b.start).getTime() - new Date(a.start).getTime())
								.map((stripe) => (
									<InfostripeListItem key={stripe.id} stripe={stripe} />
								))}
						</ul>
					</Box>
				</>
			)}
		</>
	)
}
