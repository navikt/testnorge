import React from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import Icon from '@/components/ui/icon/Icon'
import { VisningType } from '@/pages/gruppe/Gruppe'

type ToggleGroupComponentProps = {
	visning: string
	byttVisning: (value: VisningType) => void
	antallIdenter: number
	antallBestillinger: number
}

export const GruppeToggle: React.FC<ToggleGroupComponentProps> = ({
	visning,
	byttVisning,
	antallIdenter,
	antallBestillinger,
}) => {
	return (
		<ToggleGroup value={visning} onChange={byttVisning}>
			<ToggleGroup.Item
				data-testid={TestComponentSelectors.TOGGLE_VISNING_PERSONER}
				key={VisningType.VISNING_PERSONER}
				value={VisningType.VISNING_PERSONER}
			>
				<Icon
					key={VisningType.VISNING_PERSONER}
					size={13}
					kind={visning === VisningType.VISNING_PERSONER ? 'man-light' : 'man'}
				/>
				{`Personer (${antallIdenter})`}
			</ToggleGroup.Item>
			<ToggleGroup.Item
				data-testid={TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER}
				key={VisningType.VISNING_BESTILLING}
				value={VisningType.VISNING_BESTILLING}
			>
				<Icon
					key={VisningType.VISNING_BESTILLING}
					size={13}
					kind={visning === VisningType.VISNING_BESTILLING ? 'bestilling-light' : 'bestilling'}
				/>
				{`Bestillinger (${antallBestillinger})`}
			</ToggleGroup.Item>
		</ToggleGroup>
	)
}
