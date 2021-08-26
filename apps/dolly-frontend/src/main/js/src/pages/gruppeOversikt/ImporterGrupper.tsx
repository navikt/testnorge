import React, { useState } from 'react'
import * as yup from 'yup'
import useBoolean from '~/utils/hooks/useBoolean'
import { useAsync } from 'react-use'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import DollyModal from '~/components/ui/modal/DollyModal'
import { DollyApi } from '~/service/Api'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Formik, Form } from 'formik'
import './ImporterGrupper.less'
import { AsyncState } from 'react-use/lib/useAsync'

type ImporterGrupper = {
	importZIdent: Function
}

type Identer = {
	identer: Array<string>
}

type ZIdent = {
	navIdent: string
	favoritter: Array<string>
}

type SelectOptions = {
	value: string
	label: string
}

export default function ImporterGrupper({ importZIdent }: ImporterGrupper) {
	const [isImportModalOpen, openImportModal, closeImportModal] = useBoolean(false)

	const ZIdenter: AsyncState<any> = useAsync(async () => {
		const response = await DollyApi.getBrukere()
		return response.data
	}, [])

	const getZIdentOptions = () => {
		return ZIdenter.value.reduce(function(filtered: Array<SelectOptions>, ident: ZIdent) {
			if (ident.navIdent) {
				filtered.push({ value: ident.navIdent, label: ident.navIdent })
			}
			return filtered
		}, [])
	}

	const ZIdentOptions = ZIdenter.value ? getZIdentOptions() : []

	const importerZIdenter = ({ identer }: Identer) => {
		let request = identer[0]
		if (identer.length > 1) {
			for (let i = 1; i < identer.length; i++) {
				request = request.concat(`&navIdenter=${identer[i]}`)
			}
		}
		importZIdent(request)
		closeImportModal()
	}

	const validation = yup.object().shape({
		identer: yup.array().required('Velg minst én Z-ident')
	})

	return (
		<>
			<NavButton type="hoved" onClick={openImportModal} style={{ marginTop: '10px' }}>
				Importer grupper
			</NavButton>

			<DollyModal
				isOpen={isImportModalOpen}
				closeModal={closeImportModal}
				width="70%"
				overflow="auto"
			>
				<div className="importer-grupper-modal">
					<h1>Importer testdatagrupper fra Z-ident</h1>
					<h3>Velg hvilke Z-brukere du ønsker å importere. Vær klar over følgende:</h3>
					<ul>
						<li>
							Du kan importere så mange Z-brukere du ønsker. Har du flere Z-brukere vil altså alle
							dataene fra disse kunne samles på din personlige brukerkonto.
						</li>
						<li>
							Hver Z-bruker kan kun importeres av én personlig brukerkonto. Her er det førstemann
							til mølla-prinsippet som gjelder, dvs. at når du importerer en Z-bruker vil ingen
							andre kunne importere den samme.
						</li>
					</ul>

					{/* 
					// @ts-ignore */}
					<Formik initialValues={{}} onSubmit={importerZIdenter} validationSchema={validation}>
						{() => (
							<Form>
								<FormikSelect
									name="identer"
									label="Z-identer"
									options={ZIdentOptions}
									isLoading={ZIdenter.loading}
									isMulti={true}
									size="grow"
									isClearable={false}
									fastfield={false}
								/>
								<div className="import-buttons">
									<NavButton
										type="standard"
										onClick={closeImportModal}
										style={{ marginRight: '10px' }}
									>
										Avbryt
									</NavButton>
									<NavButton type="hoved" htmlType="submit">
										Importer grupper
									</NavButton>
								</div>
							</Form>
						)}
					</Formik>
				</div>
			</DollyModal>
		</>
	)
}
