import { UseFormReturn } from 'react-hook-form/dist/types'

export type KdiMeldingFieldsProps = {
	formMethods: UseFormReturn
	path: string
	fengselOptions?: Array<any>
}
