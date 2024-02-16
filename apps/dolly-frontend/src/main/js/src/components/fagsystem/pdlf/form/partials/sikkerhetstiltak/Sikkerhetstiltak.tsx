import * as React from 'react'
import { useContext, useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import _ from 'lodash'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import { Option } from '@/service/SelectOptionsOppslag'
import { isToday } from 'date-fns'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { InputWarning } from '@/components/ui/form/inputWarning/inputWarning'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialSikkerhetstiltak } from '@/components/fagsystem/pdlf/form/initialValues'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface SikkerhetstiltakProps {
	formMethods: UseFormReturn
}

export const Sikkerhetstiltak = ({ formMethods }: SikkerhetstiltakProps) => {
	const opts = useContext(BestillingsveilederContext)
	const [randomNavUsers, setRandomNavUsers] = useState([])

	const { navEnheter } = useNavEnheter()

	useEffect(() => {
		setRandomNavUsers(genererTilfeldigeNavPersonidenter())
	}, [])

	const rootPath = 'pdldata.person.sikkerhetstiltak'

	const sikkerhetstiltakListe = formMethods.watch(rootPath)

	if (!sikkerhetstiltakListe) {
		return null
	}

	const indexBeskrSikkerhetTiltak = 7

	const handleSikkerhetstiltakChange = (option: Option, idx: number) => {
		handleValueChange(option.value, 'tiltakstype', idx)
		handleValueChange(
			option.label === 'Opphørt' ? option.label : option.label.substring(indexBeskrSikkerhetTiltak),
			'beskrivelse',
			idx,
		)
	}

	const handleValueChange = (value: string, name: string, idx: number) => {
		formMethods.setValue(`${rootPath}[${idx}].${name}`, value)
	}

	return (
		<Vis attributt={rootPath} formik>
			<div className="flexbox--flex-wrap">
				<FormikDollyFieldArray
					name={rootPath}
					header="Sikkerhetstiltak"
					newEntry={initialSikkerhetstiltak}
					canBeEmpty={false}
				>
					{(path: string, idx: number) => {
						const personident = formMethods.watch(`${path}.kontaktperson.personident`)
						const gyldigFraOgMed = formMethods.watch(
							`pdldata.person.sikkerhetstiltak[${idx}].gyldigFraOgMed`,
						)
						return (
							<>
								<DollySelect
									name={`${path}.tiltakstype`}
									label="Type sikkerhetstiltak"
									options={
										opts.personFoerLeggTil
											? Options('sikkerhetstiltakType')
											: Options('sikkerhetstiltakType').filter(
													(option) => option.label !== 'Opphørt',
												)
									}
									size="large"
									onChange={(option: Option) => handleSikkerhetstiltakChange(option, idx)}
									value={formMethods.watch(`${path}.tiltakstype`)}
									isClearable={false}
								/>
								<FormikSelect
									options={
										_.isEmpty(personident)
											? randomNavUsers
											: randomNavUsers.concat({ value: personident, label: personident })
									}
									isClearable={false}
									name={`${path}.kontaktperson.personident`}
									placeholder={'Velg ...'}
									label={'Kontaktperson'}
								/>
								<FormikSelect
									name={`${path}.kontaktperson.enhet`}
									label={'NAV kontor'}
									size={'xxxlarge'}
									options={navEnheter}
								/>
								<InputWarning
									visWarning={gyldigFraOgMed && !isToday(gyldigFraOgMed)}
									warningText="TPS støtter kun sikkerhetstiltak fra gjeldende dato. Endre til dagens dato dersom et
							gyldig sikkerhetstiltak fra TPS er ønsket."
								>
									<FormikDatepicker
										name={`${path}.gyldigFraOgMed`}
										label="Sikkerhetstiltak starter"
										onChange={(date: Date) => {
											// @ts-ignore
											handleValueChange(date, 'gyldigFraOgMed', idx)
										}}
									/>
								</InputWarning>
								<FormikDatepicker
									name={`${path}.gyldigTilOgMed`}
									label="Sikkerhetstiltak opphører"
									onChange={(date: Date) => {
										// @ts-ignore
										handleValueChange(date, 'gyldigTilOgMed', idx)
									}}
								/>
								<AvansertForm path={path} kanVelgeMaster={false} />
							</>
						)
					}}
				</FormikDollyFieldArray>
			</div>
		</Vis>
	)
}
