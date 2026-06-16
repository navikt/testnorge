import { UseFormReturn } from 'react-hook-form/dist/types'

export type KdiMeldingFieldsProps = {
	path: string
	formMethods?: UseFormReturn
	erEksisterendeMelding: boolean
	fengselOptions?: Array<any>
	onSort?: () => void
	sortVersjon?: number
}
