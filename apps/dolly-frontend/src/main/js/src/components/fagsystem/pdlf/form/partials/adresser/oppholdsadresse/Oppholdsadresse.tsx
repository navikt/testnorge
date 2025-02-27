import React, { useContext, useEffect } from 'react'
import * as _ from 'lodash-es'
import {
	getInitialOppholdsadresse,
	initialMatrikkeladresse,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	MatrikkeladresseVelger,
	OppholdAnnetSted,
	UtenlandskAdresse,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface OppholdsadresseValues {
	formMethods: UseFormReturn
}

type OppholdsadresseFormValues = {
	formMethods: UseFormReturn
	path: string
	idx?: number
	identtype?: string
	identMaster?: string
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
	identMaster,
}: OppholdsadresseFormValues) => {
	const erPDL = identtype === 'NPID' || identMaster === 'PDL'
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
		}
		formMethods.trigger(path)
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
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			!erPDL && _.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_.set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			!erPDL && _.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'master', 'PDL')
		}

		formMethods.setValue(path, adresseClone)
		formMethods.trigger(path)
	}

	const adressetypeOptions = Options('adressetypeOppholdsadresse')?.filter((option) =>
		erPDL ? option.value !== 'MATRIKKELADRESSE' : option.value,
	)

	const { navnInfo, loading } = useGenererNavn()
	const navnOptions = SelectOptionsFormat.formatOptions('personnavn', navnInfo)

	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormSelect
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
			<div className="flexbox--flex-wrap">
				<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
				<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
				<OppholdAnnetSted path={`${path}.oppholdAnnetSted`} />
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
				kanVelgeMaster={valgtAdressetype !== 'MATRIKKELADRESSE' && !erPDL}
			/>
		</React.Fragment>
	)
}

export const Oppholdsadresse = ({ formMethods }: OppholdsadresseValues) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const initialMaster = opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG'

	return (
		<Kategori title="Oppholdsadresse">
			<FormDollyFieldArray
				name="pdldata.person.oppholdsadresse"
				header="Oppholdsadresse"
				newEntry={getInitialOppholdsadresse(initialMaster)}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<OppholdsadresseForm
						formMethods={formMethods}
						path={path}
						idx={idx}
						identtype={opts?.identtype}
						identMaster={opts?.identMaster}
					/>
				)}
			</FormDollyFieldArray>
		</Kategori>
	)
}
