import React, { BaseSyntheticEvent } from 'react'
import Button from '@/components/ui/button/Button'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useFieldArray } from 'react-hook-form'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

interface IdentSearchProps {
	formMethods: UseFormReturn
	onSubmit: any
}

const identerPath = 'identer'

export const Identer: React.FC<IdentSearchProps> = ({
	formMethods,
	onSubmit,
}: IdentSearchProps) => {
	// @ts-ignore
	const fieldMethods = useFieldArray({ control: formMethods.control, name: identerPath })

	return (
		<section>
			{fieldMethods.fields.map((field, index) => {
				return (
					<div key={field.id} style={{ marginTop: '10px' }}>
						<div className="flexbox--align-start" key={index}>
							<DollyTextInput
								name={`${identerPath}.${index}.fnr`}
								data-testid={CypressSelector.INPUT_TESTNORGE_FNR}
								placeholder={'Ikke spesifisert'}
								// label={'Fødselsnummer eller D-nummer'}
								style={{ width: '220px', marginBottom: '-10px' }}
								onKeyDown={(event: BaseSyntheticEvent<KeyboardEvent>) => {
									event.nativeEvent.key === 'Enter' &&
										formMethods.formState.isValid &&
										formMethods.handleSubmit(onSubmit)
								}}
							/>
							{fieldMethods.fields.length > 1 && (
								<Button
									onClick={() => fieldMethods.remove(index)}
									kind="trashcan"
									fontSize={'1.5rem'}
									style={{ marginLeft: '20px' }}
								/>
							)}
						</div>
					</div>
				)
			})}
			<Button
				onClick={() => fieldMethods.append({ fnr: '' })}
				kind="add-circle"
				style={{ margin: '0 0 10px 5px' }}
			>
				Legg til flere
			</Button>
		</section>
	)
}

export const IdenterPaths = [identerPath]
