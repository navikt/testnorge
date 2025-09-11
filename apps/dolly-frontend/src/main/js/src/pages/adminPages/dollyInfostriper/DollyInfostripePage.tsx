import React from 'react'
import { useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { CreateInfostripeForm } from '@/pages/adminPages/dollyInfostriper/CreateInfostripeForm'
import { Alert, Box } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import { InfostripeListItem } from '@/pages/adminPages/dollyInfostriper/InfostripeListItem'
import { TestComponentSelectors } from '#/mocks/Selectors'

export default function DollyInfostripePage() {
	const { infostriper, loading, error } = useDollyInfostriper()

	return (
		<>
			<h1>Infostripe-oversikt</h1>
			<p>Her finner du en oversikt over alle eksisterende infostriper som vises i Dolly.</p>
			<CreateInfostripeForm />
			{loading && (
				<div data-testid={TestComponentSelectors.LOADING_INFOSTRIPER}>
					<Loading label="Laster infostriper ..." />
				</div>
			)}
			{error && <Alert variant="warning">{`Feil ved henting av data: ${error.message}`}</Alert>}
			{infostriper?.length > 0 && (
				<Box background="surface-default" padding="4" style={{ marginTop: '1.5rem' }}>
					<ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
						{infostriper
							?.sort((a, b) => new Date(b.start).getTime() - new Date(a.start).getTime())
							.map((stripe) => (
								<InfostripeListItem key={stripe.id} stripe={stripe} />
							))}
					</ul>
				</Box>
			)}
		</>
	)
}
