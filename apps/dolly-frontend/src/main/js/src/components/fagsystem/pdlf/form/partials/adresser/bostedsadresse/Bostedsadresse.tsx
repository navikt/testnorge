import React, { useContext, useEffect } from 'react'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import {
	getInitialBostedsadresse,
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash-es'
import {
	MatrikkeladresseVelger,
	UkjentBosted,
	UtenlandskAdresse,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface BostedsadresseValues {
	formMethods: UseFormReturn
}

type BostedsadresseFormValues = {
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

export const BostedsadresseForm = ({
	formMethods,
	path,
	idx,
	identtype,
	identMaster,
}: BostedsadresseFormValues) => {
	const erPDL = identtype === 'NPID' || identMaster === 'PDL'
	useEffect(() => {
		formMethods.setValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const boadresse = formMethods.watch(path)
		if (_.get(boadresse, 'vegadresse') && _.get(boadresse, 'vegadresse') !== null) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_.get(boadresse, 'matrikkeladresse') &&
			_.get(boadresse, 'matrikkeladresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Matrikkel)
		} else if (
			_.get(boadresse, 'utenlandskAdresse') &&
			_.get(boadresse, 'utenlandskAdresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (_.get(boadresse, 'ukjentBosted') && _.get(boadresse, 'ukjentBosted') !== null) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Ukjent)
		}
		formMethods.trigger(path)
	}, [])

	const valgtAdressetype = formMethods.watch(`${path}.adressetype`)

	const getAdresseOptions = () => {
		if ((identtype && identtype !== 'FNR') || erPDL) {
			return Options('adressetypeUtenlandskBostedsadresse')
		}
		return Options('adressetypeBostedsadresse')
	}

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = formMethods.watch(path)
		const adresseClone = _.cloneDeep(adresse)

		_.set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_.set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'PDL')
		}
		if (target?.value === 'UKJENT_BOSTED') {
			_.set(adresseClone, 'ukjentBosted', initialUkjentBosted)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'master', 'FREG')
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
					options={getAdresseOptions()}
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
			{valgtAdressetype === 'UKJENT_BOSTED' && <UkjentBosted path={`${path}.ukjentBosted`} />}
			<div className="flexbox--flex-wrap">
				<FormDatepicker name={`${path}.angittFlyttedato`} label="Flyttedato" />
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
			<AvansertForm path={path} kanVelgeMaster={false} />
		</React.Fragment>
	)
}

export const Bostedsadresse = ({ formMethods }: BostedsadresseValues) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const initialMaster = opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG'

	return (
		<Kategori title="Bostedsadresse">
			<FormDollyFieldArray
				name="pdldata.person.bostedsadresse"
				header="Bostedsadresse"
				newEntry={getInitialBostedsadresse(initialMaster)}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<BostedsadresseForm
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
