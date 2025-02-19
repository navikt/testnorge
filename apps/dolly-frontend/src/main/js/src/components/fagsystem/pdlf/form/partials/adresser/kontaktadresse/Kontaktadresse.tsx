import React, { useContext, useEffect } from 'react'
import * as _ from 'lodash-es'
import {
	getInitialKontaktadresse,
	initialPostboksadresse,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	Postboksadresse,
	UtenlandskAdresse,
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
		if (_.get(kontaktadresse, 'vegadresse') && _.get(kontaktadresse, 'vegadresse') !== null) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_.get(kontaktadresse, 'utenlandskAdresse') &&
			_.get(kontaktadresse, 'utenlandskAdresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (
			_.get(kontaktadresse, 'postboksadresse') &&
			_.get(kontaktadresse, 'postboksadresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Postboks)
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
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'POSTBOKSADRESSE') {
			_.set(adresseClone, 'postboksadresse', initialPostboksadresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
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
