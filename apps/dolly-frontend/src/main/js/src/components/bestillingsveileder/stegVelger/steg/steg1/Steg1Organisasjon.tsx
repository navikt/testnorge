import React, { memo, useMemo } from 'react'
import { AttributtVelger } from './attributtVelger/AttributtVelger'
import { OrganisasjonDetaljerPanel } from './paneler/OrganisasjonDetaljer'
import type { useStateModifierFns } from '@/components/bestillingsveileder/stateModifier'

type PanelWithMeta = React.FC<any> & { heading: string; initialValues: any }

export interface Steg1OrganisasjonProps {
	stateModifier: ReturnType<typeof useStateModifierFns>
}

const PANELS: PanelWithMeta[] = [OrganisasjonDetaljerPanel]

export const Steg1Organisasjon: React.FC<Steg1OrganisasjonProps> = ({ stateModifier }) => {
	const checked = useMemo(
		() =>
			PANELS.map((p) => ({
				label: p.heading,
				values: stateModifier(p.initialValues).checked,
			})).filter((g) => g.values.length),
		[stateModifier],
	)

	return (
		<AttributtVelger checked={checked}>
			<OrganisasjonDetaljerPanel stateModifier={stateModifier} />
		</AttributtVelger>
	)
}

Steg1Organisasjon.displayName = 'Steg1Organisasjon'
export default memo(Steg1Organisasjon)
