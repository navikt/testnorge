import React, { useContext, useEffect } from 'react'
import * as _ from 'lodash-es'
import {
	getInitialKontaktadresse,
	initialPostadresseIFrittFormat,
	initialPostboksadresse,
	initialUtenlandskAdresse,
	initialUtenlandskAdresseIFrittFormat,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	PostadresseIFrittFormat,
	Postboksadresse,
	UtenlandskAdresse,
	UtenlandskAdresseIFrittFormat,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { UseFormReturn } from 'react-hook-form/dist/types'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { TestComponentSelectors } from '#/mocks/Selectors'

interface KontaktadresseValues {
	formMethods: UseFormReturn
}

type KontaktadresseFormValues = {
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

const adressetypeFieldMap: Record<string, string> = {
	VEGADRESSE: 'vegadresse',
	UTENLANDSK_ADRESSE: 'utenlandskAdresse',
	POSTBOKSADRESSE: 'postboksadresse',
	POSTADRESSE_I_FRITT_FORMAT: 'postadresseIFrittFormat',
	UTENLANDSK_ADRESSE_I_FRITT_FORMAT: 'utenlandskAdresseIFrittFormat',
}

const adressetypeInitialMap: Record<string, any> = {
	VEGADRESSE: initialVegadresse,
	UTENLANDSK_ADRESSE: initialUtenlandskAdresse,
	POSTBOKSADRESSE: initialPostboksadresse,
	POSTADRESSE_I_FRITT_FORMAT: initialPostadresseIFrittFormat,
	UTENLANDSK_ADRESSE_I_FRITT_FORMAT: initialUtenlandskAdresseIFrittFormat,
}

export const KontaktadresseForm = ({
	formMethods,
	path,
	idx,
	identtype,
	identMaster,
}: KontaktadresseFormValues) => {
	useEffect(() => {
		formMethods.setValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const kontaktadresse = formMethods.watch(path)
		const found = Object.entries(adressetypeFieldMap).find(([, field]) => {
			const v = _.get(kontaktadresse, field)
			return v !== null && v !== undefined
		})
		if (found) {
			const [value] = found
			if (value === 'VEGADRESSE') formMethods.setValue(`${path}.adressetype`, Adressetype.Veg)
			else if (value === 'UTENLANDSK_ADRESSE')
				formMethods.setValue(`${path}.adressetype`, Adressetype.Utenlandsk)
			else if (value === 'POSTBOKSADRESSE')
				formMethods.setValue(`${path}.adressetype`, Adressetype.Postboks)
			else if (value === 'POSTADRESSE_I_FRITT_FORMAT')
				formMethods.setValue(`${path}.adressetype`, Adressetype.PostadresseIFrittFormat)
			else if (value === 'UTENLANDSK_ADRESSE_I_FRITT_FORMAT')
				formMethods.setValue(`${path}.adressetype`, Adressetype.UtenlandskAdresseIFrittFormat)
		}
		formMethods.trigger(path)
	}, [])

	const valgtAdressetype = formMethods.watch(`${path}.adressetype`)

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = formMethods.watch(path)
		const adresseClone = _.cloneDeep(adresse)
		const value = target?.value || null
		_.set(adresseClone, 'adressetype', value)
		Object.values(adressetypeFieldMap).forEach((f) => _.set(adresseClone, f, undefined))
		if (value && adressetypeFieldMap[value]) {
			_.set(adresseClone, adressetypeFieldMap[value], adressetypeInitialMap[value])
		}
		formMethods.setValue(path, adresseClone)
		formMethods.trigger(path)
	}

	const { navnInfo, loading } = useGenererNavn()
	const navnOptions = SelectOptionsFormat.formatOptions('personnavn', navnInfo)

	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormSelect
					name={`${path}.adressetype`}
					label="Adressetype"
					options={Options('adressetypeKontaktadresse')}
					onChange={(target: Target) => handleChangeAdressetype(target, path)}
					size="large"
					data-testid={TestComponentSelectors.ADRESSETYPE_KONTAKTADRESSE}
				/>
			</div>
			{valgtAdressetype === 'VEGADRESSE' && (
				<VegadresseVelger
					formMethods={formMethods}
					path={`${path}.vegadresse`}
					key={`veg_${idx}`}
				/>
			)}
			{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
				<UtenlandskAdresse
					formMethods={formMethods}
					path={`${path}.utenlandskAdresse`}
					master={formMethods.watch(`${path}.master`)}
				/>
			)}
			{valgtAdressetype === 'POSTBOKSADRESSE' && (
				<Postboksadresse path={`${path}.postboksadresse`} />
			)}
			{valgtAdressetype === 'POSTADRESSE_I_FRITT_FORMAT' && (
				<PostadresseIFrittFormat path={`${path}.postadresseIFrittFormat`} />
			)}
			{valgtAdressetype === 'UTENLANDSK_ADRESSE_I_FRITT_FORMAT' && (
				<UtenlandskAdresseIFrittFormat path={`${path}.utenlandskAdresseIFrittFormat`} />
			)}
			<div className="flexbox--flex-wrap">
				<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
				<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
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
			<AvansertForm path={path} kanVelgeMaster={identMaster !== 'PDL' && identtype !== 'NPID'} />
		</React.Fragment>
	)
}

export const Kontaktadresse = ({ formMethods }: KontaktadresseValues) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const initialMaster = opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG'

	return (
		<Kategori title="Kontaktadresse">
			<FormDollyFieldArray
				name="pdldata.person.kontaktadresse"
				header="Kontaktadresse"
				newEntry={getInitialKontaktadresse(initialMaster)}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<KontaktadresseForm
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
