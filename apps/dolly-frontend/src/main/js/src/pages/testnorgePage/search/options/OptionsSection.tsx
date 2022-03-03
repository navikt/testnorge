import React from 'react'
import { OptionsPanel } from '~/pages/testnorgePage/search/options/OptionsPanel'

type OptionsSectionProps = {
	heading: string
	startOpen?: boolean
	options: React.ReactNode
	numSelected: number
	selectionColor?: string
}

export const OptionsSection: React.FC<OptionsSectionProps> = ({
	heading,
	startOpen = false,
	options,
	numSelected,
	selectionColor = 'blue',
}: OptionsSectionProps) => {
	if (numSelected > 0) {
		return (
			<OptionsPanel
				startOpen={startOpen}
				heading={heading}
				numSelected={numSelected}
				selectionColor={selectionColor}
			>
				{options}
			</OptionsPanel>
		)
	} else {
		return (
			<OptionsPanel startOpen={startOpen} heading={heading}>
				{options}
			</OptionsPanel>
		)
	}
}
