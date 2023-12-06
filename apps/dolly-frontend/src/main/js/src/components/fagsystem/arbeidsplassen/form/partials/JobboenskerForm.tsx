import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import _get from 'lodash/get'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import {
	initialJobboensker,
	initialJobboenskerVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'

export const JobboenskerForm = ({ formMethods }) => {
	const jobboenskerPath = 'arbeidsplassenCV.jobboensker'

	const setYrker = (options) => {
		const yrker = options.map((opt) => {
			return {
				title: opt.label,
				styrk08: opt.value,
			}
		})
		formMethods.setValue(`${jobboenskerPath}.occupations`, yrker)
	}

	const setOmraader = (options) => {
		const omraader = options.map((opt) => {
			return {
				location: opt.label,
				code: opt.value,
			}
		})
		formMethods.setValue(`${jobboenskerPath}.locations`, omraader)
	}

	return (
		<Vis attributt={jobboenskerPath}>
			<h3>Jobbønsker</h3>
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${jobboenskerPath}.occupations`}
					label="Jobber og yrker"
					options={Options('jobbYrke')}
					size="grow"
					isClearable={false}
					isMulti={true}
					value={_get(formMethods.getValues(), `${jobboenskerPath}.occupations`)?.map(
						(y) => y.styrk08,
					)}
					onChange={(options) => setYrker(options)}
				/>
				<FormikSelect
					name={`${jobboenskerPath}.locations`}
					label="Områder"
					options={Options('omraade')}
					size="grow"
					isClearable={false}
					isMulti={true}
					value={_get(formMethods.getValues(), `${jobboenskerPath}.locations`)?.map((o) => o.code)}
					onChange={(options) => setOmraader(options)}
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
				/>
				<FormikSelect
					name={`${jobboenskerPath}.workScheduleTypes`}
					label="Arbeidstider"
					options={Options('arbeidstid')}
					size="xxxlarge"
					isMulti={true}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${jobboenskerPath}.occupationTypes`}
					label="Ansettelsestyper"
					options={Options('ansettelsestype')}
					size="xxlarge"
					isMulti={true}
				/>
				<FormikSelect
					name={`${jobboenskerPath}.startOption`}
					label="Oppstart"
					options={Options('oppstart')}
					size="large"
				/>
			</div>
			<EraseFillButtons
				formMethods={formMethods}
				path={jobboenskerPath}
				initialErase={initialJobboensker}
				initialFill={initialJobboenskerVerdier}
			/>
		</Vis>
	)
}
