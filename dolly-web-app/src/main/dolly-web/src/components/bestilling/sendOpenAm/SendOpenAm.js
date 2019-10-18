import React from 'react'
import Knapp from 'nav-frontend-knapper'
import { useAsyncFn } from 'react-use'
import _get from 'lodash/get'
import { DollyApi } from '~/service/Api'
import DollyModal from '~/components/ui/modal/DollyModal'
import Loading from '~/components/ui/loading/Loading'
import JiraLenker from '~/components/bestilling/jiraLenker/JiraLenker'

export default function SendOpenAm({ closeModal, bestilling, getBestillinger }) {
	const [requestState, postOpenAm] = useAsyncFn(async () => {
		return DollyApi.postOpenAmBestilling(bestilling.id)
	})

	const openAmLinkArray = _get(requestState, 'value.data', []).map(response => response.message)

	const close = () => (requestState.value ? getBestillinger() : closeModal())

	return (
		<DollyModal isOpen={true} closeModal={close}>
			<h1>Send til OpenAm</h1>

			{!requestState.value && (
				<p>
					Dolly vil sende alle testidenter i bestillingen til OpenAm. Er du sikker på at du vil
					utføre dette?
				</p>
			)}

			{requestState.loading && <Loading label="sender til openAm" />}

			{requestState.value && (
				<div className="bestilling-detaljer">
					<JiraLenker openAm={openAmLinkArray} />
					<p>Sakene vil bli oppdatert i løpet av 2-3 minutter, og finnes da i kommentarfeltet.</p>

					<div className="dollymodal_buttons">
						<Knapp autoFocus type="standard" onClick={close}>
							Lukk
						</Knapp>
					</div>
				</div>
			)}

			{!requestState.value && (
				<div className="dollymodal_buttons">
					<Knapp autoFocus type="standard" onClick={closeModal}>
						Avbryt
					</Knapp>
					<Knapp type="hoved" onClick={postOpenAm}>
						Send til OpenAm
					</Knapp>
				</div>
			)}
		</DollyModal>
	)
}
