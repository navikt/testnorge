import React, { useContext, useEffect } from 'react'
import * as _ from 'lodash'
import {
	getInitialOppholdsadresse,
	initialMatrikkeladresse,
	initialOppholdAnnetSted,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	MatrikkeladresseVelger,
	OppholdAnnetSted,
	UtenlandskAdresse,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface OppholdsadresseValues {
	formMethods: UseFormReturn
}

type OppholdsadresseFormValues = {
	formMethods: UseFormReturn
	path: string
	idx?: number
	identtype?: string
}

type Target = {
	value: string
	label?: string
}

export const OppholdsadresseForm = ({
	formMethods,
	path,
	idx,
	identtype,
}: OppholdsadresseFormValues) => {
	const erNPID = identtype === 'NPID'
	useEffect(() => {
		formMethods.setValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const oppholdsadresse = formMethods.watch(path)
		if (_.get(oppholdsadresse, 'vegadresse') && _.get(oppholdsadresse, 'vegadresse') !== null) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_.get(oppholdsadresse, 'matrikkeladresse') &&
			_.get(oppholdsadresse, 'matrikkeladresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Matrikkel)
		} else if (
			_.get(oppholdsadresse, 'utenlandskAdresse') &&
			_.get(oppholdsadresse, 'utenlandskAdresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (
			_.get(oppholdsadresse, 'oppholdAnnetSted') &&
			_.get(oppholdsadresse, 'oppholdAnnetSted') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Annet)
		}
		formMethods.trigger()
	}, [])

	const valgtAdressetype = formMethods.watch(`${path}.adressetype`)

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = formMethods.watch(path)
		const adresseClone = _.cloneDeep(adresse)

		_.set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'oppholdAnnetSted', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'oppholdAnnetSted', undefined)
			!erNPID && _.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_.set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'oppholdAnnetSted', undefined)
			!erNPID && _.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'oppholdAnnetSted', undefined)
			!erNPID && _.set(adresseClone, 'master', 'PDL')
		}
		if (target?.value === 'OPPHOLD_ANNET_STED') {
			_.set(adresseClone, 'oppholdAnnetSted', initialOppholdAnnetSted)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			!erNPID && _.set(adresseClone, 'master', 'PDL')
		}

		formMethods.setValue(path, adresseClone)
		formMethods.trigger(path)
	}

	const adressetypeOptions = Options('adressetypeOppholdsadresse')?.filter((option) =>
		erNPID ? option.value !== 'MATRIKKELADRESSE' : option.value,
	)

	const { navnInfo, loading } = useGenererNavn()
	const navnOptions = SelectOptionsFormat.formatOptions('personnavn', navnInfo)

	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${path}.adressetype`}
					label="Adressetype"
					options={adressetypeOptions}
					onChange={(target: Target) => handleChangeAdressetype(target, path)}
					size="large"
				/>
			</div>
			{valgtAdressetype === 'VEGADRESSE' && (
				<VegadresseVelger
					formMethods={formMethods}
					path={`${path}.vegadresse`}
					key={`veg_${idx}`}
				/>
			)}
			{valgtAdressetype === 'MATRIKKELADRESSE' && (
				<MatrikkeladresseVelger formMethods={formMethods} path={`${path}.matrikkeladresse`} />
			)}
			{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
				<UtenlandskAdresse
					formMethods={formMethods}
					path={`${path}.utenlandskAdresse`}
					master={formMethods.watch(`${path}.master`)}
				/>
			)}
			{valgtAdressetype === 'OPPHOLD_ANNET_STED' && (
				<OppholdAnnetSted path={`${path}.oppholdAnnetSted`} />
			)}
			<div className="flexbox--flex-wrap">
				<DatepickerWrapper>
					<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
					<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
				</DatepickerWrapper>
				<DollySelect
					name={`${path}.opprettCoAdresseNavn.fornavn`}
					label="C/O adressenavn"
					options={navnOptions}
					size="xlarge"
					placeholder={getPlaceholder(formMethods.getValues(), `${path}.opprettCoAdresseNavn`)}
					isLoading={loading}
					onChange={(navn: Target) =>
						setNavn(navn, `${path}.opprettCoAdresseNavn`, formMethods.setValue)
					}
					value={formMethods.watch(`${path}.opprettCoAdresseNavn.fornavn`)}
				/>
			</div>
			<AvansertForm
				path={path}
				kanVelgeMaster={
					valgtAdressetype !== 'MATRIKKELADRESSE' &&
					valgtAdressetype !== 'OPPHOLD_ANNET_STED' &&
					!erNPID
				}
			/>
		</React.Fragment>
	)
}

export const Oppholdsadresse = ({ formMethods }: OppholdsadresseValues) => {
	const opts = useContext(BestillingsveilederContext)
	return (
		<Kategori title="Oppholdsadresse">
			<FormikDollyFieldArray
				name="pdldata.person.oppholdsadresse"
				header="Oppholdsadresse"
				newEntry={getInitialOppholdsadresse(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<OppholdsadresseForm
						formMethods={formMethods}
						path={path}
						idx={idx}
						identtype={opts?.identtype}
					/>
				)}
			</FormikDollyFieldArray>
		</Kategori>
	)
}
