import React, { PureComponent, Fragment } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import { Formik, Form, Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import './SkatteMelding.less'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import Knapp from 'nav-frontend-knapper'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'

export default class SkatteMelding extends PureComponent {
	render() {
		let initialValues = {
			morsId: 1,
			farsID: 2,
			IDType: 2,
			foedseldato: 0,
			kjoenn: 0,
			miljoe: 0
		}

		return (
			<Fragment>
				<Overskrift label={'Send skattemeldinger'} />
				<ContentContainer>
					<Formik
						initialValues={initialValues}
						render={props => {
							const { values, touched, errors, dirty, isSubmitting } = props
							return (
								<Form autoComplete="off">
									<h2>Send fødselsmelding</h2>
									<div className="skattemelding-foedselmelding-top">
										<Field name="morsId" label="MORS ID" component={FormikInput} />
										<Field name="farsId" label="FARS ID" component={FormikInput} />
										<Field
											name="BARNETS IDENTYPE"
											label="BARNETS IDENTYPE"
											component={FormikDollySelect}
										/>
										<Field name="IDType" label="BARNETS FØDSELSDATO" component={FormikDatepicker} />
										<Field name="foedseldato" label="BARNETS KJØNN" component={FormikDollySelect} />
										<Field name="miljoe" label="SEND TIL MILJØ" component={FormikDollySelect} />
									</div>
									<div className="skattemelding-foedselmelding-bottom">
										<div className="flexbox">
											<Checkbox id={'2'} label={'ARV ADRESSE FRA MOR'} />
											<Checkbox id={'3'} label={'NY ADRESSE'} />
										</div>
										<Knapp type="hoved" htmlType="submit">
											Opprett fødselsmelding
										</Knapp>
									</div>
								</Form>
							)
						}}
					/>
				</ContentContainer>
			</Fragment>
		)
	}
}
