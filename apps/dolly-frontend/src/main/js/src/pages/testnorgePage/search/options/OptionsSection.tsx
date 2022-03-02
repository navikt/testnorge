import React from 'react'
import { FormikProps } from 'formik'
import { OptionsPanel } from '~/pages/testnorgePage/search/options/OptionsPanel'

type OptionsSectionProps = {
	heading: string
	startOpen?: boolean
	options: React.ReactNode
	formikBag: FormikProps<{}>
}

export const OptionsSection = ({
	heading,
	startOpen = false,
	options,
	formikBag,
}: OptionsSectionProps) => {
	return (
		<OptionsPanel startOpen={startOpen} heading={heading}>
			{options}
		</OptionsPanel>
	)
}
