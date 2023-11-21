import React, { BaseSyntheticEvent } from 'react'
import Button from '@/components/ui/button/Button'
import { ErrorMessageWithFocus } from '@/utils/ErrorMessageWithFocus'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useFieldArray } from 'react-hook-form'

interface IdentSearchProps {
	formMethods: UseFormReturn
}

const identerPath = 'identer'

export const Identer: React.FC<IdentSearchProps> = ({ formMethods }: IdentSearchProps) => {
	// @ts-ignore
	const values = formMethods.getValues()[identerPath]
	const fieldMethods = useFieldArray({ control: formMethods.control, name: identerPath })
	return (
		<section>
			{fieldMethods.fields.map((field, index) => (
				<div key={field.id} style={{ marginTop: '10px' }}>
					{values?.length > 0 &&
						values.map((_ident: string, index: number) => (
							<div className="flexbox--align-start" key={index}>
								<div className="skjemaelement">
									<input
										data-cy={CypressSelector.INPUT_TESTNORGE_FNR}
										className="skjemaelement__input"
										placeholder={'Ikke spesifisert'}
										label={'Fødselsnummer eller D-dummer'}
										type="text"
										style={{ width: '220px' }}
										onKeyPress={(event: BaseSyntheticEvent<KeyboardEvent>) => {
											event.nativeEvent.code === 'Enter' &&
												!Object.keys(formMethods.formState.errors).length &&
												formMethods.handleSubmit()
										}}
										{...formMethods.register(`${identerPath}.${index}`)}
									/>
									<ErrorMessageWithFocus
										name={`${identerPath}.${index}`}
										className="skjemaelement__feilmelding"
										component="div"
									/>
								</div>
								{values.length > 1 && (
									<Button
										onClick={() => fieldMethods.remove(index)}
										kind="trashcan"
										fontSize={'1.5rem'}
										style={{ marginLeft: '10px' }}
									/>
								)}
							</div>
						))}
					<Button
						onClick={() => fieldMethods.append('')}
						kind="add-circle"
						style={{ margin: '0 0 10px 5px' }}
					>
						Legg til flere
					</Button>
				</div>
			))}
		</section>
	)
}

export const IdenterPaths = [identerPath]
