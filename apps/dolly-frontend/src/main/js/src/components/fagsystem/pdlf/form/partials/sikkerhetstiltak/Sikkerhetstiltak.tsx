import * as React from 'react'
import { useContext, useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import _ from 'lodash'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import { Option } from '@/service/SelectOptionsOppslag'
import { isToday } from 'date-fns'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { InputWarning } from '@/components/ui/form/inputWarning/inputWarning'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialSikkerhetstiltak } from '@/components/fagsystem/pdlf/form/initialValues'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface SikkerhetstiltakProps {
	formMethods: UseFormReturn
}

export const SikkerhetstiltakForm = ({ formMethods, path }: SikkerhetstiltakProps) => {
	const opts = useContext(BestillingsveilederContext)
	const [randomNavUsers, setRandomNavUsers] = useState([])
	const { navEnheter } = useNavEnheter()

	useEffect(() => {
		setRandomNavUsers(genererTilfeldigeNavPersonidenter())
	}, [])

	const indexBeskrSikkerhetTiltak = 7

	const handleSikkerhetstiltakChange = (option: Option) => {
		handleValueChange(option.value, 'tiltakstype')
		handleValueChange(
			option.label === 'Opphørt' ? option.label : option.label.substring(indexBeskrSikkerhetTiltak),
			'beskrivelse',
		)
	}

	const handleValueChange = (value: Date | string, name: string) => {
		formMethods.setValue(`${path}.${name}`, value)
		formMethods.trigger('pdldata.person.sikkerhetstiltak')
	}

	const personident = formMethods.watch(`${path}.kontaktperson.personident`)
	const gyldigFraOgMed = formMethods.watch(`path.gyldigFraOgMed`)

	return (
		<div className="flexbox--flex-wrap">
			<DollySelect
				name={`${path}.tiltakstype`}
				label="Type sikkerhetstiltak"
				options={
					opts?.personFoerLeggTil
						? Options('sikkerhetstiltakType')
						: Options('sikkerhetstiltakType').filter((option) => option.label !== 'Opphørt')
				}
				size="large"
				onChange={(option: Option) => handleSikkerhetstiltakChange(option)}
				value={formMethods.watch(`${path}.tiltakstype`)}
				isClearable={false}
			/>
			<FormSelect
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
			<FormSelect
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
				<FormDatepicker
					name={`${path}.gyldigFraOgMed`}
					label="Sikkerhetstiltak starter"
					onChange={(date: Date) => {
						handleValueChange(date, 'gyldigFraOgMed')
					}}
				/>
			</InputWarning>
			<FormDatepicker
				name={`${path}.gyldigTilOgMed`}
				label="Sikkerhetstiltak opphører"
				onChange={(date: Date) => {
					handleValueChange(date, 'gyldigTilOgMed')
				}}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</div>
	)
}

export const Sikkerhetstiltak = ({ formMethods }: SikkerhetstiltakProps) => {
	const rootPath = 'pdldata.person.sikkerhetstiltak'

	const sikkerhetstiltakListe = formMethods.watch(rootPath)

	if (!sikkerhetstiltakListe) {
		return null
	}

	return (
		<Vis attributt={rootPath}>
			<FormDollyFieldArray
				name={rootPath}
				header="Sikkerhetstiltak"
				newEntry={initialSikkerhetstiltak}
				canBeEmpty={false}
			>
				{(path: string) => <SikkerhetstiltakForm formMethods={formMethods} path={path} />}
			</FormDollyFieldArray>
		</Vis>
	)
}
