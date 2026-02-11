import * as React from 'react'
import { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash-es'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import { Option } from '@/service/SelectOptionsOppslag'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialSikkerhetstiltak } from '@/components/fagsystem/pdlf/form/initialValues'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface SikkerhetstiltakProps {
	formMethods: UseFormReturn
}

export const Sikkerhetstiltak = ({ formMethods }: SikkerhetstiltakProps) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const [randomNavUsers, setRandomNavUsers] = useState<{ value: string; label: string }[]>([])

	const { navEnheter } = useNavEnheter()

	useEffect(() => {
		setRandomNavUsers(genererTilfeldigeNavPersonidenter() as { value: string; label: string }[])
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

	const handleValueChange = (value: Date | string, name: string, idx: number) => {
		formMethods.setValue(`${rootPath}[${idx}].${name}`, value)
		formMethods.trigger(rootPath)
	}

	return (
		<Vis attributt={rootPath}>
			<div className="flexbox--flex-wrap">
				<FormDollyFieldArray
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
								<FormSelect
									options={
										_.isEmpty(personident)
											? randomNavUsers
											: [...randomNavUsers, { value: personident, label: personident }]
									}
									isClearable={false}
									name={`${path}.kontaktperson.personident`}
									placeholder={'Velg ...'}
									label={'Kontaktperson'}
								/>
								<FormSelect
									name={`${path}.kontaktperson.enhet`}
									label={'NAV kontor'}
									size={'xxxlarge'}
									options={navEnheter}
								/>
								<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Sikkerhetstiltak starter" />
								<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Sikkerhetstiltak opphører" />
								<AvansertForm path={path} kanVelgeMaster={false} />
							</>
						)
					}}
				</FormDollyFieldArray>
			</div>
		</Vis>
	)
}
