import React, { useState } from 'react'
import './DataVisning.less'
import 'rc-tooltip/assets/bootstrap_white.css'
import { useBoolean } from 'react-use'
import { DollyApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'
import { Ident, PdlDataWrapper } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import DollyTooltip from '~/components/ui/button/DollyTooltip'

type PdlDataVisningProps = {
	ident: Ident
}

export const ApiFeilmelding = ({ feil }) => {
	return (
		<div className="flexbox--align-center">
			<Icon size={20} kind="report-problem-circle" />
			<div>
				<pre className="api-feilmelding" style={{ fontSize: '1.25em', marginLeft: '5px' }}>
					{feil}
				</pre>
			</div>
		</div>
	)
}

export const PdlDataVisning = ({ ident }: PdlDataVisningProps) => {
	const [pdlData, setPdlData] = useState(null)
	const [pdlDataQ1, setPdlDataQ1] = useState(null)
	const [pdlLoading, setPdlLoading] = useBoolean(true)
	const [pdlLoadingQ1, setPdlLoadingQ1] = useBoolean(true)
	const [pdlError, setPdlError] = useState(null)
	const [pdlErrorQ1, setPdlErrorQ1] = useState(null)

	const setError = (pdlMiljoe: string, feilmelding: string) => {
		if (pdlMiljoe) {
			setPdlErrorQ1(feilmelding)
		} else {
			setPdlError(feilmelding)
		}
	}

	const stopLoading = (pdlMiljoe: string) => {
		if (pdlMiljoe) {
			setPdlLoadingQ1(false)
		} else {
			setPdlLoading(false)
		}
	}

	const getPersonInfo = (pdlMiljoe = null as string) => {
		if ((!pdlData && !pdlMiljoe) || (!pdlDataQ1 && pdlMiljoe)) {
			DollyApi.getPersonFraPdl(ident.ident || ident, pdlMiljoe)
				.then((response: PdlDataWrapper) => {
					if (!pdlMiljoe) {
						setPdlData(response?.data?.data)
					} else {
						setPdlDataQ1(response?.data?.data)
					}
					stopLoading(pdlMiljoe)
					const feil = response.data?.errors?.find((e) => e.path?.some((i) => i === 'hentPerson'))
					if (feil) {
						setError(pdlMiljoe, feil.message)
					}
				})
				.catch(() => {
					stopLoading(pdlMiljoe)
					setError(pdlMiljoe, 'Henting av data feilet')
				})
		}
		if (!pdlMiljoe && pdlError) {
			return <ApiFeilmelding feil={pdlError} />
		} else if (pdlMiljoe && pdlErrorQ1) {
			return <ApiFeilmelding feil={pdlErrorQ1} />
		}
		return (
			<PdlVisning
				pdlData={pdlMiljoe ? pdlDataQ1 : pdlData}
				loading={pdlMiljoe ? pdlLoadingQ1 : pdlLoading}
				miljoeVisning
			/>
		)
	}

	if (!ident) {
		return null
	}

	return (
		<div className="flexbox--flex-wrap">
			<DollyTooltip
				overlay={getPersonInfo}
				align={{
					offset: [0, -10],
				}}
				arrowContent={<div className="rc-tooltip-arrow-inner" />}
				overlayStyle={{ opacity: 1 }}
				destroyTooltipOnHide={{ keepParent: false }}
			>
				<div className="miljoe-knapp">PDL</div>
			</DollyTooltip>
			<DollyTooltip
				overlay={() => getPersonInfo('Q1')}
				align={{
					offset: [0, -10],
				}}
				arrowContent={<div className="rc-tooltip-arrow-inner" />}
				overlayStyle={{ opacity: 1 }}
				destroyTooltipOnHide={{ keepParent: false }}
			>
				<div className="miljoe-knapp">Q1</div>
			</DollyTooltip>
		</div>
	)
}
