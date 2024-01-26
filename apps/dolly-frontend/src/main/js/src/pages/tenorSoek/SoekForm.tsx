import { Form, Formik } from 'formik'
import { initialValues } from '@/pages/tenorSoek/InitialValues'
import styled from 'styled-components'
import { Accordion } from '@navikt/ds-react'
import { InntektAordningen } from '@/pages/tenorSoek/soekFormPartials/InntektAordningen'
import { useState } from 'react'
import { useTenorOversikt, useTenorSoek } from '@/utils/hooks/useTenorSoek'
import { SoekRequest } from '@/pages/dollySoek/DollySoekTypes'
import * as _ from 'lodash-es'
import { TreffListe } from '@/pages/tenorSoek/resultatVisning/TreffListe'

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px @color-bg-grey-border;
	border-radius: 4px;
`

const Soekefelt = styled.div`
	padding: 20px 15px 5px 15px;
`

// export const SoekForm = ({request, setRequest, mutate}) => {
export const SoekForm = () => {
	const [request, setRequest] = useState(null)
	// const { response, loading, error, mutate } = useTenorSoek('Noekkelinfo', request)
	const { response, loading, error, mutate } = useTenorOversikt(request)
	console.log('response: ', response) //TODO - SLETT MEG
	const handleSubmit = (request: SoekRequest) => {
		setRequest(request)
		mutate()
	}

	return (
		<>
			<SoekefeltWrapper>
				<Soekefelt>
					{/*<Formik initialValues={initialValues} onSubmit={(request) => handleSubmit(request)}>*/}
					<Formik initialValues={initialValues} onSubmit={() => console.log('submit...')}>
						{(formikBag) => {
							const handleChange = (value: any, path: string) => {
								const updatedRequest = _.set(formikBag.values, path, value)
								setRequest(updatedRequest)
								formikBag.setFieldValue(path, value)
								mutate()
							}

							const getValue = (path: string) => {
								return _.get(formikBag.values, path)
							}

							return (
								<Form className="flexbox--flex-wrap" autoComplete="off">
									<InntektAordningen
										formikBag={formikBag}
										handleChange={handleChange}
										getValue={getValue}
									/>
								</Form>
								// TODO sett inn chips her?
							)
						}}
					</Formik>
				</Soekefelt>
			</SoekefeltWrapper>
			<TreffListe response={response?.data} loading={loading} error={error} />
		</>
	)
}
