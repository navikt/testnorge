import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk, ArbeidKodeverk } from '@/config/kodeverk'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import _get from 'lodash/get'

export const JobboenskerForm = ({ formikBag }) => {
	const jobboenskerPath = 'arbeidsplassenCV.jobboensker'

	const setYrker = (options) => {
		const yrker = options.map((opt) => {
			return {
				title: opt.label,
				styrk08: opt.value,
			}
		})
		formikBag.setFieldValue(`${jobboenskerPath}.occupations`, yrker)
	}

	const setOmråder = (options) => {
		const områder = options.map((opt) => {
			return {
				location: opt.label,
				code: opt.value,
			}
		})
		formikBag.setFieldValue(`${jobboenskerPath}.locations`, områder)
	}

	return (
		<>
			<h3>Jobbønsker</h3>
			<div className="flexbox--full-width">
				{/*TODO: Må ha riktige data til lista. Sette title og styrk08*/}
				<FormikSelect
					name={`${jobboenskerPath}.occupations`}
					label="Jobber og yrker"
					kodeverk={ArbeidKodeverk.Yrker}
					size="grow"
					isClearable={false}
					isMulti={true}
					fastfield={false}
					value={_get(formikBag.values, `${jobboenskerPath}.occupations`).map((y) => y.styrk08)}
					onChange={(options) => setYrker(options)}
				/>
				{/*TODO: Må ha riktige data til lista. Sette location og code*/}
				<FormikSelect
					name={`${jobboenskerPath}.locations`}
					label="Områder"
					kodeverk={AdresseKodeverk.Kommunenummer}
					size="grow"
					isClearable={false}
					isMulti={true}
					fastfield={false}
					value={_get(formikBag.values, `${jobboenskerPath}.locations`).map((o) => o.code)}
					onChange={(options) => setOmråder(options)}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${jobboenskerPath}.workLoadTypes`}
					label="Arbeidsmengde"
					options={Options('arbeidsmengde')}
					size="medium"
					isClearable={false}
					isMulti={true}
					fastfield={false}
				/>
				<FormikSelect
					name={`${jobboenskerPath}.workScheduleTypes`}
					label="Arbeidstider"
					options={Options('arbeidstid')}
					size="xxxlarge"
					isMulti={true}
					fastfield={false}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${jobboenskerPath}.occupationTypes`}
					label="Ansettelsestyper"
					options={Options('ansettelsestype')}
					size="xxlarge"
					isMulti={true}
					fastfield={false}
				/>
				<FormikSelect
					name={`${jobboenskerPath}.startOption`}
					label="Oppstart"
					options={Options('oppstart')}
					size="large"
				/>
			</div>
		</>
	)
}
