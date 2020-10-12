package money.terra.transaction.message

import kr.jadekim.common.util.encoder.asBase64String
import money.terra.model.Coin

data class StoreCodeMessage(
    val sender: String,
    val wasmByteCode: String
) : Message("wasm/MsgStoreCode") {

    constructor(sender: String, wasmByteCode: ByteArray) : this(sender, wasmByteCode.asBase64String)
}

data class InstantiateContract(
    val owner: String,
    val codeId: String,
    val initMsg: String,
    val initCoins: List<Coin>,
    val migratable: Boolean
) : Message("wasm/MsgInstantiateContract")

data class ExecuteContract(
    val sender: String,
    val contract: String,
    val executeMsg: String,
    val coins: List<Coin>
) : Message("wasm/MsgExecuteContract")

data class MigrateContract(
    val owner: String,
    val contract: String,
    val newCodeId: String,
    val migrateMsg: String
) : Message("wasm/MsgMigrateContract")

data class UpdateContractOwner(
    val owner: String,
    val newOwner: String,
    val contract: String
) : Message("wasm/MsgUpdateContractOwner")